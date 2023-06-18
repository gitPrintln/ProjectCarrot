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
    private String imageFileName;
    private String originFileName;
    private boolean image;
    
    public String getLink() {
        if (image) {
            return imageFileName;            
        } else {
            return "noImage";
        }
    }
    
}
