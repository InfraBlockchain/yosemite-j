/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.yosemite.data.types;

import com.google.gson.annotations.Expose;
import io.yosemite.Consts;

import java.util.Objects;


public class TypePermission implements EosType.Packer {

    private final static TypePermissionName ACTIVE_PERMISSION = new TypePermissionName(Consts.ACTIVE_PERMISSION_NAME);

    @Expose
    private TypeAccountName actor;

    @Expose
    private TypePermissionName permission;

    public TypePermission(String accountName) {
        actor = new TypeAccountName(accountName);
        permission = ACTIVE_PERMISSION;
    }

    public TypePermission(String accountName, String permissionName) {
        actor = new TypeAccountName(accountName);
        permission = new TypePermissionName(permissionName);
    }

    public String getAccount() {
        return actor.toString();
    }

    public void setAccount(String accountName) {
        actor = new TypeAccountName(accountName);
    }

    public String getPermission() {
        return permission.toString();
    }

    public void setPermission(String permissionName) {
        permission = new TypePermissionName(permissionName);
    }

    @Override
    public void pack(EosType.Writer writer) {
        actor.pack(writer);
        permission.pack(writer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePermission that = (TypePermission) o;
        return actor.equals(that.actor) && permission.equals(that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actor, permission);
    }
}
