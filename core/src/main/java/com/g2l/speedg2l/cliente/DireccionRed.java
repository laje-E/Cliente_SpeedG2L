package com.g2l.speedg2l.cliente;

import com.g2l.speedg2l.utilidades.Direccion;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DireccionRed {

    private InetAddress ip;
    private int puerto;

    public DireccionRed (String ip, int puerto){
        try {
            this.ip = InetAddress.getByName(ip);
        }catch (UnknownHostException event){
            event.printStackTrace();
        }
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }
}
