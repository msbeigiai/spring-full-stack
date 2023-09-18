import {Button, Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import SidebarWithHeader from "./components/shared/Sidebar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        setLoading(true)
        getCustomers().then(res => {
            console.log(res.data);
            setCustomers(res.data);
        }).catch(err => {
            console.log(err);
        }).finally(() => {
            setLoading(false);
        })
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner />
            </SidebarWithHeader>
        )
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>No customers available!</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap spacing="35px" justify="center">
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <CardWithImage {...customer}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App;