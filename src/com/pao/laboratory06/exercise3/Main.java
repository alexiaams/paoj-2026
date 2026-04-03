package com.pao.laboratory06.exercise3;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
    // Set minimal de date pentru demonstratia capabilitatilor si sortarilor.
        Inginer[] ingineri = {
                new Inginer("Ionescu", "Mara", "0711111111", 12000, 3500),
                new Inginer("Georgescu", "Radu", "0722222222", 15000, 5500),
                new Inginer("Popescu", "Andrei", null, 10000, 2500)
        };

        System.out.println("Sortare naturala (Comparable dupa nume):");
        Arrays.sort(ingineri);
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }

        System.out.println();
        System.out.println("Sortare cu Comparator (salariu descrescator):");
        Arrays.sort(ingineri, new ComparatorInginerSalariu());
        for (Inginer inginer : ingineri) {
            System.out.println(inginer);
        }

        System.out.println();
        System.out.println("CAN_DO prin referinta PlataOnline:");
        PlataOnline plataInginer = ingineri[0];
        plataInginer.autentificare("mara.user", "parola123");
        System.out.println("Sold initial: " + plataInginer.consultareSold());
        System.out.println("Plata 1200: " + plataInginer.efectuarePlata(1200));
        System.out.println("Sold dupa plata: " + plataInginer.consultareSold());

        System.out.println();
        System.out.println("CAN_DO prin referinta PlataOnlineSMS:");
        PersoanaJuridica firmaCuTelefon = new PersoanaJuridica("Tech", "Solutions", "0733333333", 10000);
        PlataOnlineSMS plataSms = firmaCuTelefon;
        System.out.println("SMS valid trimis: " + plataSms.trimiteSMS("Plata inregistrata."));
        System.out.println("SMS gol trimis: " + plataSms.trimiteSMS("   "));
        System.out.println("Mesaje stocate: " + firmaCuTelefon.getSmsTrimise());

        PersoanaJuridica firmaFaraTelefon = new PersoanaJuridica("NoPhone", "Company", "", 7000);
        System.out.println("SMS fara telefon: " + firmaFaraTelefon.trimiteSMS("Confirmare plata"));

        System.out.println();
        System.out.println("Constanta financiara (TVA): " + ConstanteFinanciare.TVA.getValoare());

        System.out.println();
        System.out.println("Demonstrare erori:");
        try {
            // Caz invalid: user null la autentificare.
            plataInginer.autentificare(null, "1234");
        } catch (IllegalArgumentException e) {
            System.out.println("Autentificare invalida: " + e.getMessage());
        }

        try {
            // Caz invalid: client fara capabilitate SMS.
            trimiteSmsDacaPermite(plataInginer, "Mesaj catre entitate fara SMS");
        } catch (UnsupportedOperationException e) {
            System.out.println("Capabilitate SMS indisponibila: " + e.getMessage());
        }
    }

    private static boolean trimiteSmsDacaPermite(PlataOnline client, String mesaj) {
        if (!(client instanceof PlataOnlineSMS)) {
            throw new UnsupportedOperationException("Clientul nu are capabilitate SMS.");
        }
        return ((PlataOnlineSMS) client).trimiteSMS(mesaj);
    }
}
