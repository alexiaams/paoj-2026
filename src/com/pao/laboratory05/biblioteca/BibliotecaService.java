package com.pao.laboratory05.biblioteca;

import java.util.Arrays;
import java.util.Comparator;

public class BibliotecaService {
	private Carte[] carti;

	private BibliotecaService() {
		this.carti = new Carte[0];
	}

	private static class Holder {
		private static final BibliotecaService INSTANCE = new BibliotecaService();
	}

	public static BibliotecaService getInstance() {
		return Holder.INSTANCE;
	}

	public void addCarte(Carte carte) {
		if (carte == null) {
			System.out.println("Nu poti adauga o carte nula.");
			return;
		}

		Carte[] cartiResize = Arrays.copyOf(carti, carti.length + 1);
		cartiResize[carti.length] = carte;
		carti = cartiResize;

		System.out.println("Carte adaugata: " + carte);
	}

	public void listSortedByRating() {
		Carte[] copy = carti.clone();
		Arrays.sort(copy);

		System.out.println("Carti sortate dupa rating (descrescator):");
		for (Carte carte : copy) {
			System.out.println(carte);
		}
	}

	public void listSortedBy(Comparator<Carte> comparator) {
		if (comparator == null) {
			System.out.println("Comparator invalid.");
			return;
		}

		Carte[] copy = carti.clone();
		Arrays.sort(copy, comparator);

		System.out.println("Carti sortate cu comparator extern:");
		for (Carte carte : copy) {
			System.out.println(carte);
		}
	}
}

