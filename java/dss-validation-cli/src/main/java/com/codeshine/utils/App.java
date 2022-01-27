package com.codeshine.utils;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import eu.europa.esig.dss.validation.reports.Reports;

public class App {
    public static void main(String[] args) throws Exception {
        Validator validator = new Validator();
        Reports reports = validator.validate(args[0]);

        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(introspector);

        String result = mapper.writeValueAsString(reports.getSimpleReportJaxb());
        System.out.println(result);
        System.exit(1);
    }
}
