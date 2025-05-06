<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
    <#if section = "header">
        <img src="${url.resourcesPath}/img/logo.png" alt="Logo Montelago" class="logo"/>
        <h1 style="text-align:center;">ShyBlog</h1>
    <#elseif section = "form">
        <form id="kc-form-login" action="${url.loginAction}" method="post">
            <label for="username">Username o Email</label>
            <input id="username" name="username" type="text" value="${(login.username!'')}" required autofocus />

            <label for="password">Password</label>
            <input id="password" name="password" type="password" required />

            <div>
                <input type="checkbox" id="rememberMe" name="rememberMe" <#if login.rememberMe??>checked</#if> />
                <label for="rememberMe">Ricordami</label>
            </div>

            <input type="submit" id="kc-login" value="Accedi" />
        </form>

        <#if realm.registrationAllowed>
            <a href="${url.registrationUrl}">Registrati</a>
        </#if>
    </#if>
</@layout.registrationLayout>
