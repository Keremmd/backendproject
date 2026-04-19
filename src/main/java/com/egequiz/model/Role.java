package com.egequiz.model;

/**
 * Sistemde yer alan kullanıcı rolleri.
 * USER  : kayıt olup quiz çözebilen normal kullanıcı.
 * ADMIN : kategori, quiz ve soru yönetimi yapabilen yetkili kullanıcı.
 *
 * Spring Security tarafında "ROLE_USER" / "ROLE_ADMIN" şeklinde otantike edilir;
 * çevirim getAuthority() içinde yapılır (bkz. User#getAuthorities benzeri kullanım).
 */
public enum Role {
    USER,
    ADMIN
}
