package com.pao.BankingApp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Transaction {
	private static long nextId = 1;

	private final long id;
	private final long sourceAccountId;
	private final long destinationAccountId;
	private final double amount;
	private final LocalDateTime timestamp;
	private final String description;

	public Transaction(long sourceAccountId, long destinationAccountId, double amount, String description) {
		this(generateId(), sourceAccountId, destinationAccountId, amount, LocalDateTime.now(), description);
	}

	public Transaction(long id, long sourceAccountId, long destinationAccountId, double amount, LocalDateTime timestamp, String description) {
		if (sourceAccountId <= 0) {
			throw new IllegalArgumentException("Source account id must be positive");
		}
		if (destinationAccountId <= 0) {
			throw new IllegalArgumentException("Destination account id must be positive");
		}
		if (sourceAccountId == destinationAccountId) {
			throw new IllegalArgumentException("Source and destination accounts must be different");
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		this.id = id;
		updateNextId(id);
		this.sourceAccountId = sourceAccountId;
		this.destinationAccountId = destinationAccountId;
		this.amount = amount;
		this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
		this.description = (description == null || description.isBlank()) ? "Transfer" : description.trim();
	}

	private static synchronized long generateId() {
		return nextId++;
	}

	private static synchronized void updateNextId(long id) {
		if (id >= nextId) {
			nextId = id + 1;
		}
	}

	public long getId() {
		return id;
	}

	public long getSourceAccountId() {
		return sourceAccountId;
	}

	public long getDestinationAccountId() {
		return destinationAccountId;
	}

	public double getAmount() {
		return amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "Transaction{" +
				"id=" + id +
				", sourceAccountId=" + sourceAccountId +
				", destinationAccountId=" + destinationAccountId +
				", amount=" + amount +
				", timestamp=" + timestamp +
				", description='" + description + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Transaction that)) {
			return false;
		}
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
