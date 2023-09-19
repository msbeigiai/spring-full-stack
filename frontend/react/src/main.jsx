import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { ChakraProvider } from "@chakra-ui/react"
import { createStandaloneToast } from '@chakra-ui/react'
const { ToastContainer } = createStandaloneToast()
import './index.css'

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
  <React.StrictMode>
      <ChakraProvider>
          <App />
          <ToastContainer />
      </ChakraProvider>
  </React.StrictMode>
)
