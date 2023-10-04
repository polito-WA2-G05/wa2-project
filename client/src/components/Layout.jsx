// Imports
import {Outlet} from "react-router-dom"

// Components
import {Navbar, Footer} from "./index";
import {Container, Row} from "react-bootstrap";

const Layout = () => {
    return (
        <div className="d-flex flex-column justify-content-between vh-100">
            <Navbar/>
            <Container className={"flex-fill h-full d-flex flex-column align-items-center justify-content-center py-4"}>
                <Row>
                    <Outlet/>
                </Row>
            </Container>
            <Footer/>
        </div>
    )
}

export default Layout