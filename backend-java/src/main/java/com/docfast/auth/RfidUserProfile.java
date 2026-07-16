package com.docfast.auth;

import java.util.List;

public record RfidUserProfile(
    Long id,
    String nome,
    String matricula,
    String setor,
    String perfil,
    String uidRfid,
    List<String> permissions) {
}
