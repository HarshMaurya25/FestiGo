package com.example.FestiGo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload an image file to Cloudinary
     * 
     * @param file   the image file to upload
     * @param folder optional folder name in Cloudinary
     * @return the secure URL of the uploaded image
     */
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder != null ? folder : "festigo",
                "resource_type", "image");

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Upload an image file to Cloudinary with default folder
     * 
     * @param file the image file to upload
     * @return the secure URL of the uploaded image
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "festigo/events");
    }

    /**
     * Delete an image from Cloudinary by public ID
     * 
     * @param publicId the public ID of the image
     */
    @SuppressWarnings("unchecked")
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
