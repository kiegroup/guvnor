package org.guvnor.structure.server.config;

public interface PasswordService {

    String encrypt( String plainText );

    String decrypt( String encryptedText );
}
