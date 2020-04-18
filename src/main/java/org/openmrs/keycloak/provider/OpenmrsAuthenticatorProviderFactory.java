package org.openmrs.keycloak.provider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import com.google.common.collect.ImmutableMap;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.schema.Action;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.openmrs.keycloak.data.UserDao;
import org.openmrs.keycloak.models.PersonModel;
import org.openmrs.keycloak.models.PersonNameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class OpenmrsAuthenticatorProviderFactory
		implements UserStorageProviderFactory<OpenmrsAuthenticator> {

	public static final String PROVIDER_NAME = "openmrs-authentication-provider";

	private static final Logger log = LoggerFactory.getLogger(OpenmrsAuthenticatorProviderFactory.class);

	private static final List<ProviderConfigProperty> CONFIG_METADATA;

	static {
		CONFIG_METADATA = ProviderConfigurationBuilder.create()
				.property()
					.name("JDBC URL")
					.defaultValue("mysql://localhost:3306/openmrs")
					.helpText("The JDBC URL for the OpenMRS MySQL Server")
					.type(ProviderConfigProperty.STRING_TYPE)
					.add()
				.property()
					.name("User Name")
					.defaultValue("openmrs")
					.helpText("The user name of the MySQL user")
					.type(ProviderConfigProperty.STRING_TYPE)
					.add()
				.property()
					.name("Password")
					.defaultValue("openmrs")
					.helpText("The passsword for the MySQL user")
					.type(ProviderConfigProperty.PASSWORD)
					.secret(true)
					.add()
				.build();
	}

	@Override
	public OpenmrsAuthenticator create(KeycloakSession keycloakSession, ComponentModel model) {
		MultivaluedHashMap<String, String> config = model.getConfig();
		EntityManagerFactory emf = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(new PersistenceUnitInfoImpl(), ImmutableMap.<String, Object>builder()
						.put(AvailableSettings.JPA_JDBC_DRIVER, "com.mysql.jdbc.Driver")
						.put(AvailableSettings.JPA_JDBC_URL,  config.getFirst("JDBC URL"))
						.put(AvailableSettings.JPA_JDBC_USER, config.getFirst("User Name"))
						.put(AvailableSettings.JPA_JDBC_PASSWORD, config.getFirst("Password"))
				.build());
		EntityManager em = emf.createEntityManager();

		return new OpenmrsAuthenticator(keycloakSession, model, new UserDao(em));
	}

	@Override
	public String getId() {
		return PROVIDER_NAME;
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return CONFIG_METADATA;
	}

	public static class PersistenceUnitInfoImpl implements PersistenceUnitInfo {

		private static final String PERSISTENCE_UNIT_NAME = "openmrs-userstore";

		private static final List<String> CLASS_NAMES = Arrays
				.asList(UserModel.class.getName(), PersonModel.class.getName(), PersonNameModel.class.getName());

		private static final Properties SETTINGS = new Properties();

		static {
			SETTINGS.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.VALIDATE.name());
			SETTINGS.setProperty(AvailableSettings.SHOW_SQL, Boolean.FALSE.toString());
			SETTINGS.setProperty(AvailableSettings.USE_REFLECTION_OPTIMIZER, Boolean.TRUE.toString());
		}

		@Override
		public String getPersistenceUnitName() {
			return PERSISTENCE_UNIT_NAME;
		}

		@Override
		public String getPersistenceProviderClassName() {
			return HibernatePersistenceProvider.class.getName();
		}

		@Override
		public PersistenceUnitTransactionType getTransactionType() {
			return PersistenceUnitTransactionType.RESOURCE_LOCAL;
		}

		@Override
		public DataSource getJtaDataSource() {
			return null;
		}

		@Override
		public DataSource getNonJtaDataSource() {
			return null;
		}

		@Override
		public List<String> getMappingFileNames() {
			return null;
		}

		@Override
		public List<URL> getJarFileUrls() {
			return null;
		}

		@Override
		public URL getPersistenceUnitRootUrl() {
			return null;
		}

		@Override
		public List<String> getManagedClassNames() {
			return CLASS_NAMES;
		}

		@Override
		public boolean excludeUnlistedClasses() {
			return false;
		}

		@Override
		public SharedCacheMode getSharedCacheMode() {
			return SharedCacheMode.UNSPECIFIED;
		}

		@Override
		public ValidationMode getValidationMode() {
			return ValidationMode.AUTO;
		}

		@Override
		public Properties getProperties() {
			return SETTINGS;
		}

		@Override
		public String getPersistenceXMLSchemaVersion() {
			return null;
		}

		@Override
		public ClassLoader getClassLoader() {
			return null;
		}

		@Override
		public void addTransformer(ClassTransformer transformer) {

		}

		@Override
		public ClassLoader getNewTempClassLoader() {
			return null;
		}
	}
}
