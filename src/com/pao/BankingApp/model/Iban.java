package com.pao.BankingApp.model;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Iban {
	private static final String COUNTRY_CODE = "RO";
	private static final String BANK_CODE = "PAOJ";
	private static final int ACCOUNT_NUMBER_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final Set<String> GENERATED_IBANS = new HashSet<>();

	private final String value;

	public Iban(String value) {
		this.value = normalizeAndValidate(value);
	}

	public static Iban generateForAccount(long accountId) {
		if (accountId <= 0) {
			throw new IllegalArgumentException("Account id must be positive");
		}

		while (true) {
			String accountNumber = randomDigits(ACCOUNT_NUMBER_LENGTH);
			String bban = BANK_CODE + accountNumber;
			String checkDigits = computeCheckDigits(COUNTRY_CODE, bban);
			String candidate = COUNTRY_CODE + checkDigits + bban;

			synchronized (GENERATED_IBANS) {
				if (GENERATED_IBANS.add(candidate)) {
					return new Iban(candidate);
				}
			}
		}
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Iban iban)) {
			return false;
		}
		return Objects.equals(value, iban.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	private static String normalizeAndValidate(String raw) {
		if (raw == null) {
			throw new IllegalArgumentException("IBAN cannot be null");
		}

		String normalized = raw.replaceAll("\\s+", "").toUpperCase();
		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("IBAN cannot be empty");
		}

		if (!normalized.matches("^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$")) {
			throw new IllegalArgumentException("Invalid IBAN format");
		}

		if (!isValidIbanChecksum(normalized)) {
			throw new IllegalArgumentException("Invalid IBAN checksum");
		}

		return normalized;
	}

	private static boolean isValidIbanChecksum(String iban) {
		String rearranged = iban.substring(4) + iban.substring(0, 4);
		return mod97(rearranged) == 1;
	}

	private static String computeCheckDigits(String countryCode, String bban) {
		int remainder = mod97(bban + countryCode + "00");
		int checkValue = 98 - remainder;
		return String.format("%02d", checkValue);
	}

	private static int mod97(String input) {
		StringBuilder numeric = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isDigit(c)) {
				numeric.append(c);
			} else {
				numeric.append(c - 'A' + 10);
			}
		}

		int remainder = 0;
		for (int i = 0; i < numeric.length(); i++) {
			remainder = (remainder * 10 + (numeric.charAt(i) - '0')) % 97;
		}
		return remainder;
	}

	private static String randomDigits(int count) {
		StringBuilder digits = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			digits.append(RANDOM.nextInt(10));
		}
		return digits.toString();
	}
}
