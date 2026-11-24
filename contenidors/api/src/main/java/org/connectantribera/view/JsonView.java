public class JsonView {
    private final ConnexioBD configuracio;

    public JsonView(ConnexioBD configuracio) { this.configuracio = configuracio; }

    public void ok(Context ctx) {
        ctx.status(200).result("{\"estat\":\"ok\",\"baseDades\":\"ok\"}");
    }

    public void error(Context ctx, int codi, String missatge) {
        ctx.status(codi).result("{\"estat\":\"error\",\"missatge\":\"" + missatge + "\"}");
    }

}
