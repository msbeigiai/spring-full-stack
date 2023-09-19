import axios from "axios";

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASEURL}/api/v1/customers`);
    } catch (e) {
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(`${import.meta.env.VITE_API_BASEURL}/api/v1/customers`,
            customer)
    } catch (e) {
        throw e;
    }
}

export const deleteCustomer = async (id) => {
    try {
        return await axios.delete(`${import.meta.env.VITE_API_BASEURL}/api/v1/customers/${id}`,
        );
    } catch (e) {
        throw e;
    }
}