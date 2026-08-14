import { CommonModule, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { Account, BankingApiService, BankTransaction } from '../../core/banking-api.service';

registerLocaleData(localePt);

@Component({
  selector: 'app-banking-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './banking-dashboard.component.html',
  styleUrl: './banking-dashboard.component.css',
})
export class BankingDashboardComponent implements OnInit {
  readonly sourceAccountId = 1001;

  account?: Account;
  transactions: BankTransaction[] = [];
  loading = false;
  submitting = false;
  errorMessage = '';
  successMessage = '';

  transferForm = this.formBuilder.nonNullable.group({
    targetAccountId: [1002, [Validators.required, Validators.min(1)]],
    amount: [50, [Validators.required, Validators.min(0.01)]],
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly bankingApi: BankingApiService,
  ) {
  }

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      account: this.bankingApi.getAccount(this.sourceAccountId),
      statement: this.bankingApi.getStatement(this.sourceAccountId),
    }).pipe(
      finalize(() => {
        this.loading = false;
      }),
    ).subscribe({
      next: ({ account, statement }) => {
        this.account = account;
        this.transactions = statement.transactions;
      },
      error: (error: Error) => {
        this.errorMessage = `${error.message || 'Unable to reach the API.'} The free demo may need up to one minute to wake up; please retry.`;
      },
    });
  }

  submit(): void {
    if (this.transferForm.invalid) {
      this.transferForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { targetAccountId, amount } = this.transferForm.getRawValue();
    this.bankingApi.createTransfer({
      sourceAccountId: this.sourceAccountId,
      targetAccountId,
      amount,
    }, this.createIdempotencyKey()).pipe(
      finalize(() => {
        this.submitting = false;
      }),
    ).subscribe({
      next: (receipt) => {
        this.successMessage = receipt.replayed
          ? `Transferencia ${receipt.transactionId} reaproveitada pela chave de idempotencia.`
          : `Transferencia ${receipt.transactionId} criada com sucesso.`;
        this.reload();
      },
      error: (error: Error) => {
        this.errorMessage = error.message || 'Unable to complete the transfer. Please retry.';
      },
    });
  }

  signedAmount(transaction: BankTransaction): number {
    return transaction.sourceAccountId === this.sourceAccountId
      ? -transaction.amount
      : transaction.amount;
  }

  private createIdempotencyKey(): string {
    if ('randomUUID' in crypto) {
      return crypto.randomUUID();
    }
    return `transfer-${Date.now()}-${Math.random().toString(16).slice(2)}`;
  }
}
