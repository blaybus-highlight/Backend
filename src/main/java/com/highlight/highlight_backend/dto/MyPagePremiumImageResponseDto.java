package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.ProductImage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MyPagePremiumImageResponseDto {

    private List<ProductImage> premiumImages;
}
