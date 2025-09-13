package com.dbfetcher.mapper;

import com.dbfetcher.models.ProductEntity;
import commonlibs.dto.ProductDTO;

public class ProductMapper {
    public static ProductDTO toDTO(ProductEntity entity) {
        if (entity == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public static ProductEntity toEntity(ProductDTO dto) {
        if (dto == null) return null;
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return entity;
    }
}
