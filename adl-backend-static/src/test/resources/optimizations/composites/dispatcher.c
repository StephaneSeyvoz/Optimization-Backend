int METH(srvMain, main) (int argc, char *argv[]) {

  CALL(cltMain1, main)(argc, argv);

  CALL(cltMain2, main)(argc, argv);

  return 0;
}
