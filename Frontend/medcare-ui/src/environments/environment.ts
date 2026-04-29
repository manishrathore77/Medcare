export const environment = {
  production: false,
  /**
   * Use relative `/api/...` URLs so the Angular dev server can proxy to Spring Boot (see proxy.conf.json).
   * One command for clients: `npm run dev` (starts API on 8081 + UI on 4200).
   */
  apiUrl: '',
  /** Where the JVM listens; used for login hints and error messages. */
  apiBackendOrigin: 'http://localhost:8081',
};
