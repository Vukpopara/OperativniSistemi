package Projekat;

public class Main {
    public static void main(String[] args) {
        OSKernel kernel = new OSKernel(512, 2);
        kernel.boot();
    }
}