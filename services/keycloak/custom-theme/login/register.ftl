<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
    <#if section = "header">
        Registrazione
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post">
            <div>
                <label for="firstName">Nome</label>
                <input id="firstName" name="firstName" type="text" value="${(register.formData.firstName!'')}" required />
            </div>
            <div>
                <label for="lastName">Cognome</label>
                <input id="lastName" name="lastName" type="text" value="${(register.formData.lastName!'')}" required />
            </div>
            <div>
                <label for="email">Email</label>
                <input id="email" name="email" type="email" value="${(register.formData.email!'')}" required />
            </div>
            <div>
                <label for="username">Username</label>
                <input id="username" name="username" type="text" value="${(register.formData.username!'')}" required />
            </div>
            <div>
                <label for="password">Password</label>
                <input id="password" name="password" type="password" required />
            </div>
            <div>
                <label for="password-confirm">Conferma Password</label>
                <input id="password-confirm" name="password-confirm" type="password" required />
            </div>
            <div>
                <input type="submit" id="kc-register" value="Registrati" />
            </div>
        </form>
    </#if>
</@layout.registrationLayout>