package com.fict.eCommerceWebApp.services;

import com.fict.eCommerceWebApp.entities.ImageEntity;
import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.repositories.ImageRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    public static final String IMAGES_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Optional<ImageEntity> findImageById(Long id) {
        return imageRepository.findById(id);
    }

    public boolean saveImage(ListingEntity listing, MultipartFile[] images) throws IOException {
        if (!images[0].isEmpty()) {
            for (MultipartFile image : images) {
                String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
                String extension = FilenameUtils.getExtension(originalFilename);
                String newFilename = UUID.randomUUID().toString() + "." + extension;

                File uploadDir = new File(IMAGES_DIRECTORY);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                File newFile = new File(uploadDir, newFilename);
                image.transferTo(newFile);

                imageRepository.save(new ImageEntity(newFilename, listing));
            }
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean deleteImage(ImageEntity image) throws IOException {
        Optional<ImageEntity> imageOpt = imageRepository.findById(image.getId());
        if (imageOpt.isPresent()) {
            File file = new File(IMAGES_DIRECTORY + imageOpt.get().getImageName());
            if (file.exists() && file.isFile()) {
                FileUtils.delete(file);
                imageRepository.delete(image);

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}