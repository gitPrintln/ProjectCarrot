package com.carrot.nara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FileUploadDto {
    
    private String uuid;
    private String fileName;
    private boolean image;
    
    public String getLink() {
        if (image) {
            return uuid + "_" + fileName;            
        } else {
            return "noImage";
        }
    }
    
}
