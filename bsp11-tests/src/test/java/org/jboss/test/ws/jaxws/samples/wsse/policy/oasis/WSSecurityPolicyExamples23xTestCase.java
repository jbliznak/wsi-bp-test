/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.jaxws.samples.wsse.policy.oasis;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.apache.cxf.ws.security.SecurityConstants;
import org.jboss.wsf.test.CryptoHelper;
import org.jboss.wsf.test.JBossWSCXFTestSetup;
import org.jboss.wsf.test.JBossWSTest;

/**
 * WS-Security Policy examples
 *
 * From OASIS WS-SecurityPolicy Examples Version 1.0
 * http://docs.oasis-open.org/ws-sx/security-policy/examples/ws-sp-usecases-examples.html
 * 
 * @author alessio.soldano@jboss.com
 * @since 10-Sep-2012
 */
public final class WSSecurityPolicyExamples23xTestCase extends JBossWSTest
{
   private final String NS = "http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy/oasis-samples";
   private final String serviceURL = "http://" + getServerHost() + ":8080/jaxws-samples-wsse-policy-oasis-23x/";
   private final String serviceURLHttps = "https://" + getServerHost() + ":8443/jaxws-samples-wsse-policy-oasis-23x/";
   private final QName serviceName = new QName(NS, "SecurityService");

   public static Test suite()
   {
      /** System properties - currently set at testsuite start time 
      System.setProperty("javax.net.ssl.trustStore", "my.truststore");
      System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
      System.setProperty("javax.net.ssl.trustStoreType", "jks");
      System.setProperty("javax.net.ssl.keyStore", "my.keystore");
      System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
      System.setProperty("javax.net.ssl.keyStoreType", "jks");
      System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
      */
      JBossWSCXFTestSetup setup = new JBossWSCXFTestSetup(WSSecurityPolicyExamples23xTestCase.class,
            DeploymentArchives.SERVER_23X_WAR + " " + DeploymentArchives.CLIENT_JAR);
      Map<String, String> sslOptions = new HashMap<String, String>();
      sslOptions.put("server-identity.ssl.keystore-path", System.getProperty("org.jboss.ws.testsuite.server.keystore"));
      sslOptions.put("server-identity.ssl.keystore-password", "changeit");
      sslOptions.put("server-identity.ssl.alias", "tomcat");
      //enable SSL mutual authentication (https client cert is checked on server side)
      sslOptions.put("verify-client", "REQUESTED");
      sslOptions.put("authentication.truststore.keystore-path", System.getProperty("org.jboss.ws.testsuite.server.truststore"));
      sslOptions.put("authentication.truststore.keystore-password", "changeit");
      setup.setHttpsConnectorRequirement(sslOptions);
      return setup;
   }
   
   /**
    * 2.3.1.1 (WSS1.0) SAML1.1 Assertion (Bearer)
    * 
    * @throws Exception
    */
   public void test2311() throws Exception
   {
      Service service = Service.create(new URL(serviceURL + "SecurityService2311?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2311Port"), ServiceIface.class);
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.SAML_CALLBACK_HANDLER, new SamlCallbackHandler());
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL + "SecurityService2311".replaceFirst("8080", "7070"));

      assertTrue(proxy.sayHello().equals("Hello - (WSS1.0) SAML1.1 Assertion (Bearer)"));
   }

   /**
    * 2.3.1.2 (WSS1.0) SAML1.1 Assertion (Sender Vouches) over SSL
    * 
    * @throws Exception
    */
   public void test2312() throws Exception
   {
      Service service = Service.create(new URL(serviceURLHttps + "SecurityService2312?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2312Port"), ServiceIface.class);
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:1.0:cm:sender-vouches");
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      assertTrue(proxy.sayHello().equals("Hello - (WSS1.0) SAML1.1 Assertion (Sender Vouches) over SSL"));
   }
   
   /**
    * 2.3.1.3 (WSS1.0) SAML1.1 Assertion (HK) over SSL
    * 
    * @throws Exception
    */
   public void test2313() throws Exception
   {
      Service service = Service.create(new URL(serviceURLHttps + "SecurityService2313?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2313Port"), ServiceIface.class);
      Map<String, Object> reqCtx = ((BindingProvider) proxy).getRequestContext();
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:1.0:cm:holder-of-key");
      cbh.setSigned(true);
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      assertTrue(proxy.sayHello().equals("Hello -  (WSS1.0) SAML1.1 Assertion (HK) over SSL"));
   }

   /**
    * 2.3.1.4 (WSS1.0) SAML1.1 Sender Vouches with X.509 Certificates, Sign, Optional Encrypt
    * 
    * @throws Exception
    */
   public void test2314() throws Exception
   {
      Service service = Service.create(new URL(serviceURL + "SecurityService2314?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2314Port"), ServiceIface.class);
      Map<String, Object> reqCtx = ((BindingProvider) proxy).getRequestContext();
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:1.0:cm:sender-vouches");
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.ENCRYPT_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.ENCRYPT_USERNAME, "bob");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL + "SecurityService2314".replaceFirst("8080", "7070"));

      try {
         assertTrue(proxy.sayHello().equals("Hello - (WSS1.0) SAML1.1 Sender Vouches with X.509 Certificates, Sign, Optional Encrypt"));
      } catch (Exception e) {
         throw CryptoHelper.checkAndWrapException(e);
      }
   }

   /**
    * 2.3.1.5 (WSS1.0) SAML1.1 Holder of Key, Sign, Optional Encrypt
    * 
    * @throws Exception
    */
   public void test2315() throws Exception
   {
      Service service = Service.create(new URL(serviceURL + "SecurityService2315?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2315Port"), ServiceIface.class);
      Map<String, Object> reqCtx = ((BindingProvider) proxy).getRequestContext();
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:1.0:cm:holder-of-key");
      cbh.setSigned(true);
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.ENCRYPT_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.ENCRYPT_USERNAME, "bob");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL + "SecurityService2315".replaceFirst("8080", "7070"));

      try {
         assertTrue(proxy.sayHello().equals("Hello - (WSS1.0) SAML1.1 Holder of Key, Sign, Optional Encrypt"));
      } catch (Exception e) {
         throw CryptoHelper.checkAndWrapException(e);
      }
   }

   /**
    * 2.3.2.1 (WSS1.1) SAML 2.0 Bearer
    * 
    * @throws Exception
    */
   public void test2321() throws Exception
   {
      Service service = Service.create(new URL(serviceURL + "SecurityService2321?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2321Port"), ServiceIface.class);
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
      cbh.setSaml2(true);
      Map<String, Object> reqCtx = ((BindingProvider)proxy).getRequestContext();
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.ENCRYPT_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.ENCRYPT_USERNAME, "bob");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL + "SecurityService2321".replaceFirst("8080", "7070"));

      assertTrue(proxy.sayHello().equals("Hello - (WSS1.1) SAML 2.0 Bearer"));
   }

   /**
    * 2.3.2.2 (WSS1.1) SAML2.0 Sender Vouches over SSL
    * 
    * @throws Exception
    */
   public void test2322() throws Exception
   {
      Service service = Service.create(new URL(serviceURLHttps + "SecurityService2322?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2322Port"), ServiceIface.class);
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
      cbh.setSaml2(true);
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      assertTrue(proxy.sayHello().equals("Hello - (WSS1.1) SAML2.0 Sender Vouches over SSL"));
   }
   
   /**
    * 2.3.2.3 (WSS1.1) SAML2.0 HoK over SSL
    * 
    * @throws Exception
    */
   public void test2323() throws Exception
   {
      Service service = Service.create(new URL(serviceURLHttps + "SecurityService2323?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2323Port"), ServiceIface.class);
      Map<String, Object> reqCtx = ((BindingProvider) proxy).getRequestContext();
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");
      cbh.setSaml2(true);
      cbh.setSigned(true);
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      assertTrue(proxy.sayHello().equals("Hello - (WSS1.1) SAML2.0 HoK over SSL"));
   }

   /**
    * 2.3.2.4 (WSS1.1) SAML1.1/2.0 Sender Vouches with X.509 Certificate, Sign, Encrypt
    * 
    * @throws Exception
    */
   public void test2324() throws Exception
   {
      Service service = Service.create(new URL(serviceURL + "SecurityService2324?wsdl"), serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(new QName(NS, "SecurityService2324Port"), ServiceIface.class);
      Map<String, Object> reqCtx = ((BindingProvider) proxy).getRequestContext();
      SamlCallbackHandler cbh = new SamlCallbackHandler();
      cbh.setConfirmationMethod("urn:oasis:names:tc:SAML:1.0:cm:sender-vouches");
      reqCtx.put(SecurityConstants.SAML_CALLBACK_HANDLER, cbh);
      reqCtx.put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      reqCtx.put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.ENCRYPT_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/alice.properties"));
      reqCtx.put(SecurityConstants.SIGNATURE_USERNAME, "alice");
      reqCtx.put(SecurityConstants.ENCRYPT_USERNAME, "bob");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL + "SecurityService2324".replaceFirst("8080", "7070"));

      try {
         assertTrue(proxy.sayHello().equals("Hello - (WSS1.1) SAML1.1/2.0 Sender Vouches with X.509 Certificate, Sign, Encrypt"));
      } catch (Exception e) {
         throw CryptoHelper.checkAndWrapException(e);
      }
   }
}
