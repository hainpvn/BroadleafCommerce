/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.server.service.handler;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class AdminUserCustomPersistenceHandler implements CustomPersistenceHandler {
	
	private static final Log LOG = LogFactory.getLog(AdminUserCustomPersistenceHandler.class);
	
	@Resource(name="blAdminSecurityService")
	protected AdminSecurityService adminSecurityService;

	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return ceilingEntityFullyQualifiedClassname.equals(AdminUserImpl.class.getName());
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return ceilingEntityFullyQualifiedClassname.equals(AdminUserImpl.class.getName());
	}

	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			AdminUser adminInstance = (AdminUser) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(AdminUser.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
			adminInstance.setUnencodedPassword(adminInstance.getPassword());
			adminInstance.setPassword(null);
			
			adminInstance = adminSecurityService.saveAdminUser(adminInstance);
			
			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);
			
			return adminEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
	}

	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom remove not supported");
	}

	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		try {
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(AdminUser.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(AdminUser.class.getName(), persistencePerspective, dynamicEntityDao, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
			AdminUser adminInstance = (AdminUser) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
			adminInstance = (AdminUser) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
			adminInstance.setUnencodedPassword(adminInstance.getPassword());
			adminInstance.setPassword(null);
			
			adminInstance = adminSecurityService.saveAdminUser(adminInstance);
			
			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);
			
			return adminEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
	}
}
