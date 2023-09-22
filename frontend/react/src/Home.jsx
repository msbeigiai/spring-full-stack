import SidebarWithHeader from "./components/shared/Sidebar.jsx";
import React from "react";
import {Text} from "@chakra-ui/react";

const Home = () => {
    return (
        <SidebarWithHeader>
            <Text fontSize={"6xl"}>Dashboard</Text>
        </SidebarWithHeader>
    )
}

export default Home;