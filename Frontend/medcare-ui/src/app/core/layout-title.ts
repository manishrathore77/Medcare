import { DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

/** Reads `data['title']` from the deepest active child route for the top bar. */
export function layoutTitleSignal(fallback: string) {
  const router = inject(Router);
  const destroyRef = inject(DestroyRef);
  const title = signal(fallback);

  const sync = (): void => {
    let r = router.routerState.snapshot.root;
    while (r.firstChild) r = r.firstChild;
    const t = r.data['title'] as string | undefined;
    title.set(t ?? fallback);
  };

  router.events
    .pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      takeUntilDestroyed(destroyRef),
    )
    .subscribe(() => sync());

  sync();
  return title;
}
