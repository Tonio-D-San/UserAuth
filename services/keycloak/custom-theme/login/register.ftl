<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
    <#if section = "header">
        <img src="${url.resourcesPath}/img/logo.png" alt="Logo Montelago" class="logo"/>
        <h1 style="text-align:center;">Registrazione</h1>
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post">
            <label for="firstName">Nome</label>
            <input id="firstName" name="firstName" type="text" value="${(register.formData.firstName!'')}" required />

            <label for="lastName">Cognome</label>
            <input id="lastName" name="lastName" type="text" value="${(register.formData.lastName!'')}" required />

            <label for="email">Email</label>
            <input id="email" name="email" type="email" value="${(register.formData.email!'')}" required />

            <label for="username">Username</label>
            <input id="username" name="username" type="text" value="${(register.formData.username!'')}" required />

            <label for="password">Password</label>
            <input id="password" name="password" type="password" required />

            <label for="password-confirm">Conferma Password</label>
            <input id="password-confirm" name="password-confirm" type="password" required />

            <input type="submit" id="kc-register" value="Registrati" />
        </form>
    </#if>
</@layout.registrationLayout>
