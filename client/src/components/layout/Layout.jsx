// Imports
import { Outlet } from "react-router-dom"
import { Container, Row } from "react-bootstrap";

// Components
import { Navbar, Footer } from "@components/layout";

const Layout = () => {
    return (
        <Container fluid className="d-flex flex-column justify-content-between p-0 mx-auto" style={{minHeight: "100vh"}}>
            <Navbar />
            <Row className='align-items-center flex-fill p-3 m-0 h-100'>
                <Outlet />
            </Row>
            <Footer />
        </Container>
    )
}

export default Layout