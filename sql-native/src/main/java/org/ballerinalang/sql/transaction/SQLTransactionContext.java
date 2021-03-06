/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.sql.transaction;

import io.ballerina.runtime.api.ErrorCreator;
import io.ballerina.runtime.api.StringUtils;
import io.ballerina.runtime.transactions.BallerinaTransactionContext;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.xa.XAResource;

/**
 * {@code SQLTransactionContext} transaction context for SQL transactions.
 *
 * @since 2.0.0
 */
public class SQLTransactionContext implements BallerinaTransactionContext {
    private Connection conn;
    private XAResource xaResource;

    public SQLTransactionContext(Connection conn, XAResource resource) {
        this.conn = conn;
        this.xaResource = resource;
    }

    public SQLTransactionContext(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw ErrorCreator.createError(StringUtils.fromString("transaction commit failed:" + e.getMessage()));
        }
    }

    @Override
    public void rollback() {
        try {
            if (!conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw ErrorCreator.createError(StringUtils.fromString("transaction rollback failed:" + e.getMessage()));
        }
    }

    @Override
    public void close() {
        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw ErrorCreator.createError(StringUtils.fromString("connection close failed:" + e.getMessage()));
        }
    }

    @Override
    public XAResource getXAResource() {
        return this.xaResource;
    }
}
