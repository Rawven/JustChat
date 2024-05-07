import {md5} from "js-md5";
import {Host} from "@/main";

export default {
    ws: {},
    id:0,
    setWs(newWs) {
        this.ws = newWs
    },
    setId(newId) {
        this.id = newId
    },
}
