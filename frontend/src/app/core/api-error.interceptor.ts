import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

interface ApiErrorBody {
  message?: string;
  error?: string;
}

export const apiErrorInterceptor: HttpInterceptorFn = (request, next) => {
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const body = error.error as ApiErrorBody | undefined;
      const message = body?.message ?? 'Nao foi possivel concluir a operacao.';
      return throwError(() => new Error(message));
    }),
  );
};
