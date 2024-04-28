package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanUp implements InitializingBean {
	
	private final EntityManager entityManager;
	private List<String> tableNames = new ArrayList<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		tableNames = entityManager.getMetamodel().getEntities().stream()
				.filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
				.map(entityType -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName())).collect(Collectors.toList());
	}
	
	@Transactional
	public void truncateAllEntity() {
		System.out.println("66666");
		entityManager.flush();
		System.out.println("777777");
		
		System.out.println("88888");
		entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED;").executeUpdate();
		for(String tableName : tableNames) {
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
		}
		System.out.println("99999");
		
		
		entityManager.createNativeQuery("SET CONSTRAINTS ALL IMMEDIATE;").executeUpdate();
	}
}
