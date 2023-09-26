import React, {useCallback} from 'react'
import {useDropzone} from 'react-dropzone'
import {Box} from "@chakra-ui/react";
import {uploadCustomerProfilePicture} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

export default function MyDropzone({ customerId, fetchCustomers }) {
    const onDrop = useCallback(acceptedFiles => {
        const formData = new FormData();
        formData.append("file", acceptedFiles[0])

        uploadCustomerProfilePicture(
            customerId,
            formData
        ).then(() => {
            successNotification("Success", "Profile picture uploaded successfully.");
            fetchCustomers();
        }).catch((err) => {
            errorNotification("Error", "Profile picture failed to upload.");
            console.log(err)
        })
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={"100%"}
             textAlign={"center"}
             border={"dashed"}
             borderColor={"gray.200"}
             borderRadius={"3xl"}
             p={6}
             rounded={"md"}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the files here ...</p> :
                    <p>Drag 'n' drop some files here, or click to select files</p>
            }
        </Box>
    )
}