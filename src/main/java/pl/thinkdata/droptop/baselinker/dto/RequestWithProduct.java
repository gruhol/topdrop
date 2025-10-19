package pl.thinkdata.droptop.baselinker.dto;

import pl.thinkdata.droptop.database.model.Product;

public record RequestWithProduct(AddProductRequest request, Product product) {}