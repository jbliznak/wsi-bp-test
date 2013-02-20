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
package org.jboss.test.ws.jaxws.bp12.wsa.test1198;

import javax.xml.ws.WebFault;

@WebFault(name = "SignatureHeaderFaultContractRpc", targetNamespace = "http://example.org/signature")
public class SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage extends Exception
{
   private org.jboss.test.ws.jaxws.bp12.wsa.test1198.SignatureHeaderFaultContractRpc signatureHeaderFaultContractRpc;

   public SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage()
   {
      super();
   }

   public SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage(String message)
   {
      super(message);
   }

   public SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage(String message, Throwable cause)
   {
      super(message, cause);
   }

   public SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage(String message,
         org.jboss.test.ws.jaxws.bp12.wsa.test1198.SignatureHeaderFaultContractRpc signatureHeaderFaultContractRpc)
   {
      super(message);
      this.signatureHeaderFaultContractRpc = signatureHeaderFaultContractRpc;
   }

   public SignatureRpcLiteralSign2SignatureHeaderRpcFaultFaultMessage(String message,
         org.jboss.test.ws.jaxws.bp12.wsa.test1198.SignatureHeaderFaultContractRpc signatureHeaderFaultContractRpc, Throwable cause)
   {
      super(message, cause);
      this.signatureHeaderFaultContractRpc = signatureHeaderFaultContractRpc;
   }

   public org.jboss.test.ws.jaxws.bp12.wsa.test1198.SignatureHeaderFaultContractRpc getFaultInfo()
   {
      return this.signatureHeaderFaultContractRpc;
   }
}
