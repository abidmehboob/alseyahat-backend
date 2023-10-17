package com.alseyahat.app.feature.attachment.facade.impl;

import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.commons.Constants;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.attachment.dto.AttachmentResponse;
import com.alseyahat.app.feature.attachment.dto.AttachmentUploadRequest;
import com.alseyahat.app.feature.attachment.dto.AttachmentUploadResponse;
import com.alseyahat.app.feature.attachment.facade.AttachmentFacade;
import com.alseyahat.app.feature.attachment.properties.FileStorageProperties;
import com.alseyahat.app.feature.attachment.service.AttachmentService;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.wallet.repository.entity.QWallet;
import com.alseyahat.app.feature.wallet.repository.entity.Wallet;
import com.alseyahat.app.feature.wallet.service.WalletService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttachmentFacadeImpl implements AttachmentFacade {

	FileStorageProperties fileStorageProperties;

	AttachmentService attachmentService;

	ModelMapper modelMapper;

	EmployeeService employeeService;
	CustomerService customerService;
	WalletService walletService;

//    @SneakyThrows
	@Override
	public AttachmentResponse createAttachment(final String module, final MultipartFile file,
			final String fileExtension) {
		final String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {

			try {
				FTPClient client = createSession();
				String directory = getDirectory(module);
				String fileName = generateFileName(fileExtension);
				boolean isSwitched = switchDirectory(directory, client);
				log.trace("Uploading file [{}] in directory [{}]", fileName, directory);
				if (!isSwitched) {
					throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.directory_not_created");
				}
				client.enterLocalPassiveMode(); // important!
				client.setFileType(FTP.BINARY_FILE_TYPE);

				boolean result = client.storeFile(fileName, file.getInputStream());

//	            byte[] bytes = Base64.getDecoder().decode(file.getBytes());
//	            boolean isUploaded = client.storeFile(fileName, new ByteArrayInputStream(bytes));
				client.logout();
//	            if(!result){
//	                throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.file_not_uploaded");
//	            }
				log.trace("File uploaded [{}] in directory [{}]", fileName, directory);
				AttachmentResponse attachmentResponse = new AttachmentResponse();
				directory = directory.replace("public_html/", "");
				attachmentResponse.setPath(generateHTTPPath(directory, fileName));

				final Customer customer = customerService
						.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
				Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
				if (wallet.isPresent()) {
					Wallet walletEntity = new Wallet();
					walletEntity.setId(wallet.get().getId());
					walletEntity.setUserId(wallet.get().getUserId());
					walletEntity.setCurrency(wallet.get().getCurrency());
					walletEntity.setBalance(wallet.get().getBalance());
					walletEntity.setUserType("Customer");
					walletEntity.setLastUpdatedBy(username);
					walletEntity.setPaymentReceipt(fileName);
					walletEntity.setDateCreated(wallet.get().getDateCreated());
					walletEntity.setLastUpdated(new Date());
					walletService.save(walletEntity);
				}
				return attachmentResponse;
			} catch (Exception e) {
				log.error("Uploading customer recipt error [{}]", username);
				log.error("Uploading customer recipt error [{}]", e);
			}

		}else {
			try {
				FTPClient client = createSession();
				String directory = getDirectory(module);
				String fileName = generateFileName(fileExtension);
				boolean isSwitched = switchDirectory(directory, client);
				log.trace("Uploading file [{}] in directory [{}]", fileName, directory);
				if (!isSwitched) {
					throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.directory_not_created");
				}
				client.enterLocalPassiveMode(); // important!
				client.setFileType(FTP.BINARY_FILE_TYPE);

				boolean result = client.storeFile(fileName, file.getInputStream());

//	            byte[] bytes = Base64.getDecoder().decode(file.getBytes());
//	            boolean isUploaded = client.storeFile(fileName, new ByteArrayInputStream(bytes));
				client.logout();
//	            if(!result){
//	                throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.file_not_uploaded");
//	            }
				log.trace("File uploaded [{}] in directory [{}]", fileName, directory);
				AttachmentResponse attachmentResponse = new AttachmentResponse();
				directory = directory.replace("public_html/", "");
				attachmentResponse.setPath(generateHTTPPath(directory, fileName));
			
				return attachmentResponse;
			} catch (Exception e) {
				log.error("Uploading customer recipt error [{}]", username);
				log.error("Uploading customer recipt error [{}]", e);
			}
		}
		return null;
//	        }else
//	        	 throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);

	}

	@SneakyThrows
	private String getDirectory(String module) {
		String directory = null;
		switch (module) {
		case Constants.HOTEL_IMAGE:
			directory = fileStorageProperties.getDirectory().getHotelImages();
			break;
		case Constants.DEAL_IMAGE:
			directory = fileStorageProperties.getDirectory().getDealImages();
			break;
		case Constants.SIGHT_SEEING_IMAGE:
			directory = fileStorageProperties.getDirectory().getSightSeeingImages();
			break;
		case Constants.PRIVATE_HIRED_IMAGE:
			directory = fileStorageProperties.getDirectory().getSightSeeingImages();
			break;
		case Constants.CUSTOMER_PAYMENT_IMAGE:
			directory = fileStorageProperties.getDirectory().getCustomerPaymentImages();
			break;
		case Constants.RESTAURANT_MENU_IMAGE:
			directory = fileStorageProperties.getDirectory().getRestaurantMenuImages();
			break;
		default:
			throw new IllegalStateException("Unexpected attachment module type: " + module);
		}
		return directory;
	}

	@SneakyThrows
	public boolean switchDirectory(String dirPath, FTPClient client) {
		String[] pathElements = dirPath.split("/");
		if (pathElements != null && pathElements.length > 0) {
			for (String singleDir : pathElements) {
				boolean existed = client.changeWorkingDirectory(singleDir);
				if (!existed) {
					boolean created = client.makeDirectory(singleDir);
					if (created) {
						log.trace("Directory created with name [{}]", singleDir);
						client.changeWorkingDirectory(singleDir);
					} else {
						log.trace("Directory couldn't be created with name [{}]", singleDir);
						return false;
					}
				}
			}
		}
		return true;
	}

	public String generateFileName(String fileExtension) {
		return UUID.randomUUID().toString() + Constants.DOT + fileExtension;
	}

	public String generateFileName() {
		return UUID.randomUUID().toString();
	}

	public String generateHTTPPath(String directory, String fileName) {
		return fileStorageProperties.getAccessUrl().substring(0, fileStorageProperties.getAccessUrl().length() - 1)
				+ ":8080/" + directory + Constants.SLASH + fileName;
	}

	@SneakyThrows
	public FTPClient createSession() {
		FTPClient client = new FTPClient();
		client.connect("168.235.86.155");
		boolean login = client.login("admin_web", "web123");
		if (!login) {
			throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.ftp_login_failed");
		}
		client.setFileType(FTP.BINARY_FILE_TYPE);
		return client;
	}

	@Override
	public AttachmentUploadResponse uploadAttachment(AttachmentUploadRequest attachmentUploadRequest) {
		log.trace("File Upload Feign Client Request {[]}", attachmentUploadRequest);

		try {

			String[] strings = attachmentUploadRequest.getBase64image().split(",");
			String extension;
			switch (strings[0]) {// check image's extension
			case "data:image/jpeg;base64":
				extension = "jpeg";
				break;
			case "data:image/png;base64":
				extension = "png";
				break;
			default:// should write cases for more images types
				extension = "jpg";
				break;
			}
			FTPClient client = createSession();
			String directory = getDirectory(attachmentUploadRequest.getModule());
			String fileName = generateFileName(extension);
			String imageDataBytes = attachmentUploadRequest.getBase64image()
					.substring(attachmentUploadRequest.getBase64image().indexOf(",") + 1);
			Decoder decoder = Base64.getDecoder();
			InputStream stream = new ByteArrayInputStream(decoder.decode(imageDataBytes.getBytes()));
			boolean isSwitched = switchDirectory(directory, client);
			log.trace("Uploading file [{}] in directory [{}]", directory);
			if (!isSwitched) {
				throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.directory_not_created");
			}
			client.enterLocalPassiveMode(); // important!
			client.setFileType(FTP.BINARY_FILE_TYPE);
//            InputStream inputStream = new ByteArrayInputStream(attachmentUploadRequest.getBase64image().getBytes(Charset.forName("UTF-8")));

			boolean result = client.storeUniqueFile(fileName, stream);

//            byte[] bytes = Base64.getDecoder().decode(file.getBytes());
//            boolean isUploaded = client.storeFile(fileName, new ByteArrayInputStream(bytes));
			client.logout();
//            if(!result){
//                throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.file_not_uploaded");
//            }
			log.trace("File uploaded [{}] in directory [{}]", directory);
			AttachmentUploadResponse attachmentResponse = new AttachmentUploadResponse();
			directory = directory.replace("public_html/", "");
			attachmentResponse.setPath(generateHTTPPath(directory, fileName));
			return attachmentResponse;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
//        }else
//        	 throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
//		 final String username = AppUtils.getUserNameFromAuthentication();
//	        final Optional<Employee> employee = employeeService.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
//	        if(employee.isPresent()){
//	        	 return buildAttachmentUploadResponse(attachmentService.uploadFile(attachmentUploadRequest));
//	        }else
//           	 throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	private AttachmentUploadResponse buildAttachmentUploadResponse(Map<String, Object> response) {
		log.trace("File Upload Feign Client Response {[]}", response);
		return modelMapper.map(response, AttachmentUploadResponse.class);
	}

	@Override
	public AttachmentUploadResponse uploadAttachment(String module, MultipartFile file) {
		log.trace("File Upload Feign Client Request {[]}", module, file);
//		 final String username = AppUtils.getUserNameFromAuthentication();
//	        final Optional<Employee> employee = employeeService.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
//	        if(employee.isPresent()){
		return buildAttachmentUploadResponse(attachmentService.uploadFile(buildAttachmentUploadRequest(module, file)));
//	        }else
//           	 throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@SneakyThrows
	private AttachmentUploadRequest buildAttachmentUploadRequest(final String module, final MultipartFile file) {
		AttachmentUploadRequest response = new AttachmentUploadRequest();
		response.setModule(module);
		final String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
		response.setBase64image("data:image/" + ext + ";base64," + Base64.getEncoder().encodeToString(file.getBytes()));
		return response;
	}

	@Override
	public AttachmentResponse deleteAttachment(String module, String fileName) {
		try {
			FTPClient client = createSession();
			String directory = getDirectory(module);
			boolean isSwitched = switchDirectory(directory, client);
			log.trace("Uploading file [{}] in directory [{}]", fileName, directory);
			if (!isSwitched) {
				throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.directory_not_created");
			}
			client.enterLocalPassiveMode(); // important!
//            client.setFileType(FTP.BINARY_FILE_TYPE);

			boolean result = client.deleteFile(generateHTTPPath(directory, fileName));

			client.logout();
//            if(!result){
//                throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "error.file_not_uploaded");
//            }
			log.trace("File uploaded [{}] in directory [{}]", fileName, directory);
			AttachmentResponse attachmentResponse = new AttachmentResponse();
			directory = directory.replace("public_html/", "");
			attachmentResponse.setPath(generateHTTPPath(directory, fileName));
			return attachmentResponse;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
//        }else
//        	 throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}
}
