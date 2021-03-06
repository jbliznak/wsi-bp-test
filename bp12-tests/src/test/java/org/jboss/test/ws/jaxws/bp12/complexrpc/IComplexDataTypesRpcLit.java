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
package org.jboss.test.ws.jaxws.bp12.complexrpc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.soap.Addressing;

@WebService(targetNamespace = "http://tempuri.org/", name = "IComplexDataTypesRpcLit")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Addressing
public interface IComplexDataTypesRpcLit {

    @WebResult(name = "RetArrayString1DResult", targetNamespace = "http://tempuri.org/", partName = "RetArrayString1DResult")
    @Action(input = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayString1D", output = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayString1DResponse")
    @WebMethod(operationName = "RetArrayString1D", action = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayString1D")
    public StringArray retArrayString1D(
        @WebParam(partName = "inArrayString1D", name = "inArrayString1D")
        StringArray inArrayString1D
    );

    @WebResult(name = "RetArrayInt1DResult", targetNamespace = "http://tempuri.org/", partName = "RetArrayInt1DResult")
    @Action(input = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayInt1D", output = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayInt1DResponse")
    @WebMethod(operationName = "RetArrayInt1D", action = "http://tempuri.org/IComplexDataTypesRpcLit/RetArrayInt1D")
    public IntArray retArrayInt1D(
        @WebParam(partName = "inArrayInt1D", name = "inArrayInt1D")
        IntArray inArrayInt1D
    );
}
