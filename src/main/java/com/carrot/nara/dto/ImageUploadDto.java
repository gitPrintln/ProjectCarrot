package com.carrot.nara.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ImageUploadDto {
    
    private List<MultipartFile> files;
    
}
