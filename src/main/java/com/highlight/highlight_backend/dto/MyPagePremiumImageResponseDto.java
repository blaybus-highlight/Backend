package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.ProductImage;
import lombok.Setter;

import java.util.List;

@Setter
public class MyPagePremiumImageResponseDto {

    private List<ProductImage> premiumImages;
}
