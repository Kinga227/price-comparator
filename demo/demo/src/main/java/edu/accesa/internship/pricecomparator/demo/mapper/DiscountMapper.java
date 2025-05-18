package edu.accesa.internship.pricecomparator.demo.mapper;

import edu.accesa.internship.pricecomparator.demo.dto.DiscountDTO;
import edu.accesa.internship.pricecomparator.demo.model.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.category", target = "category")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    DiscountDTO modelToDto(Discount discount);
}
