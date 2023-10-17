package com.alseyahat.app.commons;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import lombok.extern.slf4j.Slf4j;

import static com.alseyahat.app.commons.Constants.DOUBLE_COLUN;
import static com.alseyahat.app.commons.Constants.TRUE;
import static com.alseyahat.app.commons.Constants.FALSE;
import static com.alseyahat.app.commons.Constants.DATE_TIME_FORMAT;
import static com.alseyahat.app.commons.Constants.ROOM_CHARGES;


@Slf4j
public class CustomPredicates {

	static Map<String, Operator> operators = ImmutableMap.<String, Operator>builder()
			.put("eq", Ops.EQ)
			.put("ne", Ops.NE)
			.put("gt", Ops.GT)
			.put("lt", Ops.LT)
			.put("gte", Ops.GOE)
			.put("lte", Ops.LOE)
			.put("like", Ops.STRING_CONTAINS_IC)
			.put("in", Ops.IN)
			.put("isNotNull", Ops.IS_NOT_NULL)
			.put("isNull", Ops.IS_NULL)
			.build(); 
	
	public static <T> Predicate toPredicate(final Map<String, Object> parameters, final EntityPathBase<T> entityBasePath) {
		final Map<String, Object> predicatesParameters = removePaginationParameters(parameters);
		final PathBuilder<Object> entityPath = new PathBuilder<Object>(entityBasePath.getClass(),entityBasePath.getMetadata());
				
		BooleanBuilder builder = new BooleanBuilder(entityBasePath.isNotNull());
		
		predicatesParameters.entrySet().stream().forEach(entry -> { 
			if(entry.getKey().contains(DOUBLE_COLUN)) { 
				final String searchField = entry.getKey().substring(0, entry.getKey().lastIndexOf(DOUBLE_COLUN));
				Operator operator = operators.get(entry.getKey().substring(entry.getKey().lastIndexOf(DOUBLE_COLUN) + 1));
				PathBuilder<Object> columnPath = entityPath.get(searchField); 
				
				builder.and(Expressions.predicate(
						operator, 
						columnPath, 
						Expressions.constant(castValues(entry.getValue(),searchField))
				));
				
			}else {
				builder.and(Expressions.predicate(
						Ops.EQ, 
						entityPath.get(entry.getKey()), 
						Expressions.constant(castValues(entry.getValue(),entry.getKey()))));
			}
		});
		return builder;
	}
	
	
	private static Object castValues(Object value, String columnName) {
		Object response = null;
		if(NumberUtils.isDigits((String) value)) { 
			response = NumberUtils.toInt(String.valueOf(value));
		}
		if(NumberUtils.isCreatable((String) value)) { 
			response = Double.valueOf((String) value);
		} 
		if(((String) value).equalsIgnoreCase(TRUE) || ((String) value).equalsIgnoreCase(FALSE)){ 
			response = BooleanUtils.toBoolean((String) value);
		}
		if(isValid((String) value)){
	        response = Timestamp.valueOf(getTimestampFormat(value.toString()));
		}
		
		if(columnName.equalsIgnoreCase(ROOM_CHARGES)) {
			response = String.valueOf(value);
		}
				
		return response == null? value:response;
	}
	
	private static String getTimestampFormat(String date) { 
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    	LocalDateTime now = LocalDateTime.parse(date, formatter);
        return now.format(formatter);
    }
	
	public static boolean isValid(final String date) {
        boolean valid = false;
        try {
        	LocalDate.parse(date,
                    DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
            );
            valid = true;
        } catch (DateTimeParseException e) {
            valid = false;
        }
        return valid;
    }
	
	private static Map<String, Object> removePaginationParameters(final Map<String, Object> parameters){
		parameters.remove("page");
		parameters.remove("size");
		parameters.remove("sort");
		return parameters;
	}
	
}

