import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { BankingApiService } from './banking-api.service';

describe('BankingApiService', () => {
  let service: BankingApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BankingApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(BankingApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('sends idempotency key when creating a transfer', () => {
    service.createTransfer({
      sourceAccountId: 1001,
      targetAccountId: 1002,
      amount: 25,
    }, 'idem-angular').subscribe();

    const request = http.expectOne('http://localhost:8080/api/v1/transfers');
    expect(request.request.method).toBe('POST');
    expect(request.request.headers.get('Idempotency-Key')).toBe('idem-angular');
    expect(request.request.body).toEqual({
      sourceAccountId: 1001,
      targetAccountId: 1002,
      amount: 25,
    });

    request.flush({
      transactionId: 10,
      sourceAccountId: 1001,
      targetAccountId: 1002,
      amount: 25,
      status: 'COMPLETED',
      createdAt: '2026-05-22T12:00:00Z',
      replayed: false,
    });
  });
});
