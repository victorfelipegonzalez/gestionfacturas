package com.gestionfacturas.gestionfacturasapi.models;

import lombok.Data;

@Data
public class ResponseModel {
    private int success;
    private String message;
    private Object data;
}
