import axios from "axios";

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASEURL}/api/v1/customers`);
    } catch (e) {
        throw e;
    }

}