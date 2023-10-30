<template>
    <q-page class="flex flex-center column">

    </q-page>
</template>

<style></style>

<script>

    export default {
        name: 'Logout', data() {
            return {

            };
        },
        mounted(){
          let scriptAuth = document.createElement('script')
          scriptAuth.setAttribute('src', 'https://accounts.google.com/gsi/client')
          document.head.appendChild(scriptAuth)

          scriptAuth.onload=async ()=>{
            google.accounts.id.disableAutoSelect();

            localStorage.removeItem('token');
            localStorage.removeItem('rol');
            localStorage.clear();

            //Redirect
            this.$router.push('/login');
          }
        }
    }
</script>
