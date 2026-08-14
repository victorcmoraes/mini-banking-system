import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Account {
  id: number;
  ownerName: string;
  balance: number;
}

export interface StatementResponse {
  accountId: number;
  transactions: BankTransaction[];
}

export interface BankTransaction {
  id: number;
  sourceAccountId: number;
  targetAccountId: number;
  amount: number;
  status: 'COMPLETED' | 'FAILED';
  createdAt: string;
}

export interface TransferRequest {
  sourceAccountId: number;
  targetAccountId: number;
  amount: number;
}

export interface TransferReceipt extends TransferRequest {
  transactionId: number;
  status: 'COMPLETED' | 'FAILED';
  createdAt: string;
  replayed: boolean;
}

@Injectable({ providedIn: 'root' })
export class BankingApiService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {
  }

  getAccount(accountId: number): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/api/v1/accounts/${accountId}`);
  }

  getStatement(accountId: number): Observable<StatementResponse> {
    return this.http.get<StatementResponse>(`${this.baseUrl}/api/v1/accounts/${accountId}/statement`);
  }

  createTransfer(request: TransferRequest, idempotencyKey: string): Observable<TransferReceipt> {
    return this.http.post<TransferReceipt>(`${this.baseUrl}/api/v1/transfers`, request, {
      headers: {
        'Idempotency-Key': idempotencyKey,
      },
    });
  }
}
