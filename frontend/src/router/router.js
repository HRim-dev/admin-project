import { createWebHistory, createRouter } from "vue-router";
//import HelloWorld from "@/components/HelloWorld.vue";
// import Home from "@/pages/Home.vue";
import MainLayout from "@/layout/MainLayout.vue";
import SignUp from "@/pages/user/SignUp.vue";
import Login from "@/pages/user/Login.vue";

const routes = [
    // {
    //     path: "/",
    //     name: "HelloWorld",
    //     component: HelloWorld,
    // },
    // {
    //     path: "/",  // Home.vue로 이동할 Path
    //     name: "Home",  // router name
    //     component: Home,  // Path로 이동될 Component
    // },
    {
        path: "/",
        name: "mainlayout",
        component: MainLayout,
    },
    {
        path: "/signup",  // Home.vue로 이동할 Path
        name: "Signup",  // router name
        component: SignUp,  // Path로 이동될 Component
    },
    {
        path: "/login",  // Home.vue로 이동할 Path
        name: "Login",  // router name
        component: Login,  // Path로 이동될 Component
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
 