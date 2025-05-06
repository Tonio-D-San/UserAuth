<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
    <#if section = "header">
        Accesso
    <#elseif section = "form">
        <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
            <div>
                <label for="username">Username o Email</label>
                <input id="username" name="username" type="text" autofocus required value="${(login.username!'')}" />
            </div>
            <div>
                <label for="password">Password</label>
                <input id="password" name="password" type="password" required />
            </div>
            <div>
                <input type="checkbox" id="rememberMe" name="rememberMe" <#if login.rememberMe??>checked</#if> />
                <label for="rememberMe">Ricordami</label>
            </div>
            <div>
                <input type="submit" id="kc-login" value="Accedi" />
            </div>
            <#if realm.registrationAllowed>
                <div>
                    <a href="${url.registrationUrl}">Registrati</a>
                </div>
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>
