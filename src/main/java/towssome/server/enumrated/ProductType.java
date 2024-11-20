package towssome.server.enumrated;

public enum ProductType {

    가전디지털("가전/디지털"),
    뷰티("뷰티"),
    생활용품("생활용품"),
    스포츠건강("스포츠/건강"),
    식품("식품"),
    패션잡화("패션/잡화"),
    문구오피스("문구/오피스"),
    문화생활("문화생활")
    ;
    private final String product;

    ProductType(String product) {
        this.product = product;
    }

    public String getValue() {
        return product;
    }
}
