import { boot } from 'quasar/wrappers';
// @ts-ignore
import GoogleSignInPlugin from "vue3-google-signin"


export default boot(({ app }) => {
  //clientId: '174833690753-1sjgl4lvn2ttdu55r4h5k5erl0rj9otv.apps.googleusercontent.com',
  app.use(GoogleSignInPlugin, {
    clientId: process.env.GOOGLE_CLIENT_ID,
  });
});
