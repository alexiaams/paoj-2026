package com.pao.BankingApp.model;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Card {
	private static long nextId = 1;
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final Set<String> GENERATED_CARD_NUMBERS = new HashSet<>();

	private final long id;
	private final String cardNumber;
	private final String holderName;
	private final YearMonth expirationDate;
	private final String cvv;
	private final long accountId;
	private boolean active;

	public Card(String holderName, long accountId) {
		if (holderName == null || holderName.isBlank()) {
			throw new IllegalArgumentException("Holder name cannot be empty");
		}
		if (accountId <= 0) {
			throw new IllegalArgumentException("Account id must be positive");
		}

		this.id = generateId();
		this.holderName = holderName.trim();
		this.accountId = accountId;
		this.cardNumber = generateCardNumber(this.id, this.accountId);
		this.expirationDate = YearMonth.now().plusYears(4);
		this.cvv = generateCvv();
		this.active = true;
	}

	private static synchronized long generateId() {
		return nextId++;
	}

	private static String generateCardNumber(long cardId, long accountId) {
		while (true) {
			StringBuilder first15 = new StringBuilder("400000");
			for (int i = 0; i < 9; i++) {
				first15.append(RANDOM.nextInt(10));
			}

			int checkDigit = computeLuhnCheckDigit(first15.toString());
			String candidate = first15.append(checkDigit).toString();

			synchronized (GENERATED_CARD_NUMBERS) {
				if (GENERATED_CARD_NUMBERS.add(candidate)) {
					return candidate;
				}
			}
		}
	}

	private static String generateCvv() {
		return String.format("%03d", RANDOM.nextInt(1000));
	}

	private static int computeLuhnCheckDigit(String first15Digits) {
		int sum = 0;
		boolean doubleDigit = true;

		for (int i = first15Digits.length() - 1; i >= 0; i--) {
			int digit = first15Digits.charAt(i) - '0';
			if (doubleDigit) {
				digit *= 2;
				if (digit > 9) {
					digit -= 9;
				}
			}
			sum += digit;
			doubleDigit = !doubleDigit;
		}

		return (10 - (sum % 10)) % 10;
	}

	public long getId() {
		return id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getMaskedCardNumber() {
		return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
	}

	public String getHolderName() {
		return holderName;
	}

	public YearMonth getExpirationDate() {
		return expirationDate;
	}

	public String getCvv() {
		return cvv;
	}

	public long getAccountId() {
		return accountId;
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		this.active = false;
	}

	public void activate() {
		this.active = true;
	}

	public boolean isExpired() {
		return expirationDate.isBefore(YearMonth.now());
	}

	@Override
	public String toString() {
		return "Card{" +
				"id=" + id +
				", cardNumber='" + getMaskedCardNumber() + '\'' +
				", holderName='" + holderName + '\'' +
				", expirationDate=" + expirationDate +
				", accountId=" + accountId +
				", active=" + active +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Card card)) {
			return false;
		}
		return Objects.equals(cardNumber, card.cardNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cardNumber);
	}
}
