package com.brh.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@SuppressWarnings("unused")
public class Argon2Utility {
    private static final Config config = ConfigProvider.getConfig();

    private static final int saltLength = config.getValue("argon2.salt.length",Integer.class);
    private static final int hashLength = config.getValue("argon2.hash.length",Integer.class);
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id,saltLength,hashLength);
    private static final int iterations = config.getValue("argon2.iterations",Integer.class);
    private static final int memory = config.getValue("argon2.memory",Integer.class);
    private static final int threadNumber = config.getValue("argon2.thread.number",Integer.class);

    public static String hash(char[] boundaryHash){
        try{
            return argon2.hash(iterations,memory,threadNumber,boundaryHash);
        }finally {
            argon2.wipeArray(boundaryHash);
        }
    }

    public static boolean check(String dbHash, char[] boundaryHash){
        try{
            return argon2.verify(dbHash,boundaryHash);
        }finally {
            argon2.wipeArray(boundaryHash);
        }
    }
}